using educashAPI.Data;
using educashAPI.Data.Entity;
using educashAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Reflection.Metadata.Ecma335;

namespace educashAPI.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class CategorieController : ControllerBase
    {
        private readonly ILogger<CategorieController> _logger;
        private readonly EducashDbContext _educashDbContext;
        public CategorieController(ILogger<CategorieController> logger, EducashDbContext educashDbContext)
        {
            _logger = logger;
            _educashDbContext = educashDbContext;
        }

        //Get list of categories 
        [HttpGet("GetCategories")]
        public IEnumerable<Categories> Get(string token)
        {
            //Find user by passed in token
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token == token);

            //Check to see if user is null
            if (user == null)
            {
                return new List<Categories>();
            }

            //Find all default entries and entries created by user
            var result = _educashDbContext.categories.Where(x => x.UserId == null || x.UserId == user.UserID).ToList();

            //Return entries
            return result;
        }


        //Add a custom categorie
        [HttpPost("NewCategorie")]
        public IEnumerable<Categories> Post(string token,AddNewCategorieModel newCat)
        {
            //Find user by passed in token 
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token == token);

            //Check to see if user is null
            if (user == null) 
            {
                return new List<Categories>();
            }


            //Add infomation
            var newCategorie = new Categories
            {
                UserId = user.UserID,
                CategorieName = newCat.CategorieName
            };

            _educashDbContext.categories.Add(newCategorie);

            //Try and save changes to the database
            try
            {
                _educashDbContext.SaveChanges();
                return new List<Categories> { newCategorie };
            }
            //Catch if fails
            catch (Exception ex) 
            {
                Response.StatusCode = 401;
                return new List<Categories>();
            }
        }

        //Remove custom categories 
        [HttpDelete("catdelete")]
        public bool Delete(int id, string token) 
        {
            //Find user by passed id
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token == token);

            //Check to see if user is null
            if (user == null)
            {
                Response.StatusCode = 401;
                return false;
            }

            //If user not null find categorie by userId and categorieId
            var categorie = _educashDbContext.categories.SingleOrDefault(x => x.CategorieId == id && x.UserId == user.UserID);

            //Check to see if categoie has been found
            if (categorie != null)
            {
                _educashDbContext.categories.Remove(categorie);

                //Try and save the changes to database
                try
                {
                    _educashDbContext.SaveChanges();
                    return true;
                }
                catch (Exception ex)
                {
                    Response.StatusCode = 401;
                    return false;
                }
            }
            //If categorie is null then return false
            else
            {
                Response.StatusCode = 401;
                return false;
            }
        }
    }
}
