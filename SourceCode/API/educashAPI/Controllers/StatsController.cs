using educashAPI.Data;
using educashAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Identity.Client;
using Microsoft.VisualBasic;
using System.Diagnostics.CodeAnalysis;
using System.Text.RegularExpressions;

namespace educashAPI.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class StatsController : ControllerBase
    {
        private readonly ILogger<StatsController> _logger;
        private readonly EducashDbContext _educashDbContext;
        public StatsController(ILogger<StatsController> logger, EducashDbContext educashDbContext)
        {
            _logger = logger;
            _educashDbContext = educashDbContext;
        }

        //Weekly Stats
        [HttpGet("Weekly")]
        public IEnumerable<StatsReturnModel> GetWeekly(string token, DateTime startDate)
        {
            //Find user by passed in token
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token == token);

            if (user == null)
            {
                return new List<StatsReturnModel>();
            }

            //Find total spend for week
            var weeklyTotal = _educashDbContext.transactions
                .Where(x => x.TransactionDate.Date >= startDate.Date && x.TransactionDate.Date <= startDate.Date.AddDays(7) && x.UserId == user.UserID)
                .Sum(x => x.TransactionAmount);

            //Find top Trans
            var findTopTrans = _educashDbContext.transactions
                .Where(x => x.TransactionDate.Date >= startDate.Date && x.TransactionDate.Date <= startDate.Date.AddDays(7) && x.UserId == user.UserID)
                .GroupBy(x => new
                {
                    x.Categorie.CategorieName,
                    x.Categorieid
                })
                .Select(x => new StatsReturnModel
                {
                    CatName = x.Key.CategorieName,
                    CatTotal = x.Sum(y => y.TransactionAmount),
                    catId = x.Key.Categorieid,
                    Percentage = Math.Round((x.Sum(y => y.TransactionAmount) / weeklyTotal) * 100, 3)
                })
                .OrderByDescending(x => x.CatTotal)
                .Take(5)
                .ToList();

            //Get total for top 5 categories
            var topTransTotal = findTopTrans.Sum(x => x.CatTotal);

            //Find Top Trans Percentage 
            var topTransPercentageTotal = findTopTrans.Sum(x => x.Percentage);

            //Find ammount for other total 
            var otherTotal = weeklyTotal - topTransTotal;

            //Add the new found total to the model and return it as other
            findTopTrans.Add(new StatsReturnModel
            {
                CatName = "Other",
                CatTotal = otherTotal,
                Percentage = Math.Round(100 - topTransPercentageTotal, 3)
            });


            return findTopTrans;
        }

        //Monthly Stats
        [HttpGet("Monthly")]
        public IEnumerable<StatsReturnModel> GetMonthly(string token, int month, int year)
        {
            //Find user by passed in token 
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token == token);

            //Check to see if user is null
            if (user == null)
            {
                return new List<StatsReturnModel>();
            }
            //Get total spend for the entered month
            var monthTotal = _educashDbContext.transactions
                .Where(x => x.TransactionDate.Month == month && x.TransactionDate.Year == year && x.UserId == user.UserID)
                .Sum(x => x.TransactionAmount);

            //Find the top 5 transaction categoies and their total ammounts
            var findTopTrans = _educashDbContext.transactions
                .Where(x => x.TransactionDate.Month == month && x.TransactionDate.Year == year && x.UserId == user.UserID)
                .GroupBy(x => new
                {
                    x.Categorie.CategorieName,
                    x.Categorieid
                })
                .Select(x => new StatsReturnModel
                {
                    CatName = x.Key.CategorieName,
                    CatTotal = x.Sum(y => y.TransactionAmount),
                    catId = x.Key.Categorieid,
                    Percentage = Math.Round((x.Sum(y => y.TransactionAmount) / monthTotal) * 100 ,3)
                })
                .OrderByDescending(x => x.CatTotal)
                .Take(5)
                .ToList();

            //Get total for top 5 categories
            var topTransTotal = findTopTrans.Sum(x => x.CatTotal);

            //Find total percentage of top trans
            var topTransPercentageTotal = findTopTrans.Sum(x => x.Percentage);
            
            //Find the ammount of the rest of the categories excluding the top 5
            var otherTotal = monthTotal - topTransTotal;

            //Add the new found total to the model and return it as other
            findTopTrans.Add(new StatsReturnModel
            {
                CatName = "Other",
                CatTotal = otherTotal,
                Percentage = Math.Round(100 - topTransPercentageTotal,3)
            });

            return findTopTrans;
        }

        //Get the termly stats
        [HttpGet("Termly")]
        public IEnumerable<StatsReturnModel> GetTermly(string token, int termSelect)
        {
            //Find user by passed in token
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token == token);

            //Check to see if user is null
            if (user == null)
            {
                return new List<StatsReturnModel>();
            }

            var startMonth = 0;
            var endMonth = 0;
            var year = DateTime.Now.Year;

            //Depending on which term has been selected set the start and end months
            switch (termSelect)
            {
                case 1:
                    startMonth = 9; //Sept
                    endMonth = 12; // Dec
                    break;

                case 2:
                    startMonth = 1; //Jan
                    endMonth = 3; //March
                    break;

                case 3:
                    startMonth = 4; //Apil
                    endMonth = 6; //June
                    break;

                default:
                    throw new Exception("Term not valid");
            }

            //Find the total spent in the term
            var termTotal = _educashDbContext.transactions
                .Where(x => x.TransactionDate.Month >= startMonth && x.TransactionDate.Month <= endMonth && x.TransactionDate.Year == year && x.UserId == user.UserID)
                .Sum(x => x.TransactionAmount);

            
            //Find the top 5 categories for the selected term
            var findTopTrans = _educashDbContext.transactions
                .Where(x => x.TransactionDate.Month >= startMonth && x.TransactionDate.Month <= endMonth && x.TransactionDate.Year == year && x.UserId == user.UserID)
                .GroupBy(x => new
                {
                    x.Categorie.CategorieName,
                    x.Categorieid
                })
                .Select(x => new StatsReturnModel
                {
                    CatName = x.Key.CategorieName,
                    CatTotal = x.Sum(y => y.TransactionAmount),
                    catId = x.Key.Categorieid,
                    Percentage = Math.Round((x.Sum(y => y.TransactionAmount) / termTotal) * 100, 3)

                })
                .OrderByDescending(x => x.CatTotal)
                .Take(5)
                .ToList();

            //Find the top 5 total spend
            var topTransTotal = findTopTrans.Sum(x => x.CatTotal);

            //find the total percentage of the top 5
            var topTransPercentage = findTopTrans.Sum(x => x.Percentage);

            //find the rest of the categories total spend
            var otherTotal = termTotal - topTransTotal;

            //Add the rest total to the model as other
            findTopTrans.Add(new StatsReturnModel
            {
                CatName = "Other",
                CatTotal = otherTotal,
                Percentage = Math.Round(100 - topTransPercentage, 3)
            });

            return findTopTrans;
        }
    }
}
