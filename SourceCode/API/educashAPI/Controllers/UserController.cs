using educashAPI.Data;
using educashAPI.Data.Entity;
using educashAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;
using Microsoft.VisualBasic;
using System.Xml;

namespace educashAPI.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class UserController : ControllerBase
    {
        private readonly ILogger<UserController> _logger;
        private readonly EducashDbContext _educashDbContext;
        public UserController(ILogger<UserController> logger, EducashDbContext educashDbContext)
        {
            _logger = logger;
            _educashDbContext = educashDbContext;
        }

        //Get Userinfo when passing in user token
        [HttpGet(Name = "GetUsers")]
        public IEnumerable<UserProfile> Get(string token)
        {
            if (token == null)
            {
                return new List<UserProfile>();
            }
            else
            {
                var result = _educashDbContext.users.SingleOrDefault(x => x.Token == token);
                return result == null ? new List<UserProfile>() : new List<UserProfile> { result };
            }
        }


        //Create new user
        [HttpPost(Name = "NewUser")]
        public string Post(CreateUserModel user)
        {
            var newUserProfile = new UserProfile
            {
                Username = user.Username,
                pin = user.pin,
                Pword = user.Pword,
                Token = Guid.NewGuid().ToString(),
                LIP = false,
                negAllowed = true
            };

            _educashDbContext.users.Add(newUserProfile);

            //Try Catch to send nice message to front end if dupe entries exist
            try
            {
                _educashDbContext.SaveChanges();

                //Find user that has just been created 
                var newUser = _educashDbContext.users.SingleOrDefault(x => x.UserID == newUserProfile.UserID);

                //Create a new account total for the user that has just been created
                var newAccount = new AccountTotals
                {
                    UserId = newUser.UserID,
                    CurrentAmount = 0,
                    Savings = 0
                };

                //Add and try and save changes to the database
                _educashDbContext.accounts.Add(newAccount);

                try
                {
                    _educashDbContext.SaveChanges();
                    return newUserProfile.Token;
                }
                catch (Exception ex)
                {
                    Response.StatusCode = 401;
                    return string.Empty;
                }
            }
            catch (Exception ex)
            {
                Response.StatusCode = 401;
                return string.Empty;
            }

        }

        //Change users password
        [HttpPut("ChangePassword")]
        public IEnumerable<UserProfile> changePassword(string token, string password)
        {
            //Find user by passed in token
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token == token);

            //Check to see if user is null
            if (user == null)
            {
                Response.StatusCode = 401;
                return new List<UserProfile> { user };
            }

            //Make the change
            user.Pword = password;

            //Try catch to make sure save changes to database are done no errors
            try
            {
                _educashDbContext.SaveChanges();
                return new List<UserProfile> { user };
            }
            catch (Exception ex)
            {
                return new List<UserProfile> { user };
            }

        }

        //Change users pin
        [HttpPut("ChangePin")]
        public IEnumerable<UserProfile> ChangePin(string token, string pin)
        {
            //Find user by passed in token 
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token == token);

            //Check to see if user is null
            if (user == null)
            {
                Response.StatusCode = 401;
                return new List<UserProfile> { user };
            }

            //make the change
            user.pin = pin;

            //Try and save chanegs to database
            try
            {
                _educashDbContext.SaveChanges();
                return new List<UserProfile> { user };
            }
            catch (Exception ex)
            {
                return new List<UserProfile> { user };
            }
        }

        //Change LIP status
        [HttpPut("ChangeLIP")]
        public IEnumerable<UserProfile> ChangeLIP(string token, bool LIP)
        {
            //Find user by passed in token
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token == token);

            //Check to see if user is null
            if (user == null)
            {
                Response.StatusCode = 401;
                return new List<UserProfile>() { user };
            }

            //Make change
            user.LIP = LIP;


            //Try and save chanegs to database
            try
            {
                _educashDbContext.SaveChanges();
                return new List<UserProfile> { user };
            }
            catch(Exception ex)
            {
                return new List<UserProfile> { user };
            }
        }

        //Change is negAllowed
        [HttpPut("ChangeNeg")]
        public IEnumerable<UserProfile> ChangeNeg(string token, string neg)
        {
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token == token);

            if (user == null)
            {
                return new List<UserProfile>();
            }

            user.negAllowed = bool.Parse(neg);

            try
            {
                _educashDbContext.SaveChanges();
                return new List<UserProfile> { user };
            }
            catch (Exception ex)
            {
                return new List<UserProfile> { user };
            }
        }

        //Delete User
        [HttpDelete(Name = "DeleteUser")]
        public bool Delete(string token)
        {
            var userProfile = _educashDbContext.users.SingleOrDefault(x => x.Token == token);

            _educashDbContext.users.Remove(userProfile);
            try
            {
                _educashDbContext.SaveChanges();
                return true;
            }
            catch (Exception)
            {
                return false;
            }
        }

    }
}
