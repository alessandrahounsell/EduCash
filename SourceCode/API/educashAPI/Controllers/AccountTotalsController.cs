using educashAPI.Data;
using educashAPI.Data.Entity;
using educashAPI.Models;
using Microsoft.AspNetCore.Mvc;

namespace educashAPI.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class AccountTotalsController : ControllerBase
    {
        private readonly ILogger<AccountTotalsController> _logger;
        private readonly EducashDbContext _educashDbContext;
        public AccountTotalsController(ILogger<AccountTotalsController> logger, EducashDbContext educashDbContext)
        {
            _logger = logger;
            _educashDbContext = educashDbContext;
        }

        //Get the accounts for the user
        [HttpGet(Name = "GetAmmounts")]
        public IEnumerable<AccountTotals> Get(string token)
        {
            //find user by passed in token 
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token == token);

            //if null then return empty list
            if (user == null)
            {
                return new List<AccountTotals>();
            }
            //otherwise returns accounts
            else
            {
                return _educashDbContext.accounts.Where(x => x.UserId == user.UserID).ToList();
            }
        }

        //Add accounts
        [HttpPost(Name = "AddAccounts")]
        public IEnumerable<AccountTotals>? Post(NewAccountsModel account, string token)
        {

            //Find user using passed in token
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token == token);

            //Check if user is authorised 
            if (user == null)
            {
                Response.StatusCode = 401;
                return null;
            }
            
            //If user not null then create new account 
            var newAccount = new AccountTotals()
            {
                UserId = user.UserID,
                CurrentAmount = account.CurrentAmmount,
                Savings = account.Savings
            };

            //Add and save the infomation to the database
            _educashDbContext.accounts.Add(newAccount);
            _educashDbContext.SaveChanges();

            //Return the account totals for the user
            return new List<AccountTotals> { newAccount };
        }

        //Update accounts
        [HttpPut(Name = "UpdateAccounts")]
        public IEnumerable<AccountTotals>? Put(UpdateAccountModel account, string token)
        {
            //Find user by passed in token
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token == token);

            //Check to see if user is null
            if (user == null)
            {
                Response.StatusCode = 401;
                return null;
            }

            //Find account by using userid
            var accountProfile = _educashDbContext.accounts.SingleOrDefault(x => x.UserId == user.UserID);

            accountProfile.CurrentAmount = account.CurrentAmmount;
            accountProfile.Savings = account.Savings;

            //Save the changes to the databaes
            _educashDbContext.SaveChanges();

            //Return the user account totals
            return new List<AccountTotals> { accountProfile };
        }

        //Delete Account
        [HttpDelete(Name = "DeleteAccount")]
        public bool Delete(string token)
        {
            //Find user by passed in token
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token == token);

            //Check to see if user is null
            if (user == null)
            {
                Response.StatusCode = 401;
                return false;
            }

            //Find account by user id
            var account = _educashDbContext.accounts.SingleOrDefault(x => x.UserId == user.UserID);

            //Check to see if account is not null
            if (account != null)
            {
                //Remove the account
                _educashDbContext.accounts.Remove(account);
                
                try
                {
                    _educashDbContext.SaveChanges();
                    return true;
                }
                catch (Exception ex)
                {
                    return false;
                }
            }
            else
            {
                return false;
            }

        }
    }
}
