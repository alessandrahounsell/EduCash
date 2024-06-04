using educashAPI.Data;
using educashAPI.Data.Entity;
using educashAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.ActionConstraints;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.SqlServer.Query.Internal;

namespace educashAPI.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class TransactionController : ControllerBase
    {
        private readonly ILogger<TransactionController> _logger;
        private readonly EducashDbContext _educashDbContext;
        public TransactionController(ILogger<TransactionController> logger, EducashDbContext educashDbContext)
        {
            _logger = logger;
            _educashDbContext = educashDbContext;
        }

        //View all user transactions
        [HttpGet(Name = "GetUserTransactions")]
        public IEnumerable<TransactionTable> Get(string token)
        {
            //Find user by passed in token
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token == token);

            //Find tansaction by user id
            var transactions = _educashDbContext.transactions.Where(x => x.UserId == user.UserID).OrderBy(y => y.TransactionDate).Include(x => x.Categorie).ToList();

            //Check to see if user is null
            if (user == null)
            {
                Response.StatusCode = 401;
                return new List<TransactionTable>();
            }
            else
            {
                return transactions;
            }

        }

        //Add new transaction
        [HttpPost(Name = "AddTransaction")]
        public IEnumerable<TransactionTable>? Post(AddTransactionModel transaction, string token)
        {
            //Find user by passed in token
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token == token);

            //Check to see if user is null
            if (user == null)
            {
                Response.StatusCode = 401;
                return null;
            }

            //Find the categoire by passed in id
            var cat = _educashDbContext.categories.SingleOrDefault(x => x.CategorieId == transaction.CategorieId);

            //Find user accounts for late use
            var account = _educashDbContext.accounts.FirstOrDefault(x => x.UserId == user.UserID);

            //Check to see if null
            if (cat == null)
            {
                Response.StatusCode = 401;
                return null;
            }

            //Set the infomation
            var newTransaction = new TransactionTable()
            {
                UserId = user.UserID,
                Categorieid = transaction.CategorieId,
                Categorie = cat,
                Take = transaction.Take,
                Name = transaction.Name,
                TransactionAmount = transaction.TransactionAmmount,
                TransactionDate = DateTime.Now,
            };


            //Add the changes to the database
            _educashDbContext.transactions.Add(newTransaction);

            //Try and save changes 
            try
            {
                //Check to see if they have enough money to make transaction if negAllowed is false
                if (user.negAllowed == false && transaction.Take == true)
                {
                    if ((account.CurrentAmount - transaction.TransactionAmmount) < 0)
                    {
                        throw new Exception("Not enough money to make transaction");
                    }
                }

                //Add or remove money from accounts
                //If false take add money to accounts
                if (transaction.Take == false)
                {
                    account.CurrentAmount = account.CurrentAmount + transaction.TransactionAmmount;
                }
                //If true take the money from account
                else if (transaction.Take == true)
                {
                    account.CurrentAmount = account.CurrentAmount - transaction.TransactionAmmount;
                }

                //Save changes to database and return the new transaction
                _educashDbContext.SaveChanges();

                return new List<TransactionTable> { newTransaction };

            }
            catch (Exception ex)
            {
                Response.StatusCode = 401;
                return new List<TransactionTable>();
            }
        }

        //Delete a transaction
        [HttpDelete(Name = "DeleteTransaction")]
        public bool Delete(int id, string token)
        {
            //Find the user by passed in token
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token == token);

            if (user == null)
            {
                Response.StatusCode = 401;
                return false;
            }

            //Find transaction
            var transaction = _educashDbContext.transactions.SingleOrDefault(x => x.TransactionId == id && x.UserId == user.UserID);
            var account = _educashDbContext.accounts.SingleOrDefault(x => x.UserId == user.UserID);

            //Check if transaction is nulll
            if (transaction != null)
            {

                account.CurrentAmount = account.CurrentAmount + transaction.TransactionAmount;

                _educashDbContext.transactions.Remove(transaction);

                //Try and save change to database
                try
                {
                    _educashDbContext.SaveChanges();
                    return true;
                }
                //Return HTTP 401 and false if change fails
                catch (Exception ex)
                {
                    Response.StatusCode = 401;
                    return false;
                }
            }
            //Return false if transaction does not exist
            else
            {
                return false;
            }
        }
    }
}
