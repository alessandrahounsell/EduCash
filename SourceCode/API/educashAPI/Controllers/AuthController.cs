using educashAPI.Data;
using educashAPI.Models;
using Microsoft.AspNetCore.Mvc;
using System.Runtime.CompilerServices;

namespace educashAPI.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class AuthController : ControllerBase
    {
        private readonly ILogger<AuthController> _logger;
        private readonly EducashDbContext _educashDbContext;
        public AuthController(ILogger<AuthController> logger, EducashDbContext educashDbContext)
        {
            _logger = logger;
            _educashDbContext = educashDbContext;
        }

        //Enter user detai,details
        [HttpPost("Login")]
        public string Login(UserLoginModel userLogin)
        {
            //Check if entered credentials match that of what is in the database
            var user = _educashDbContext.users.SingleOrDefault(x => x.Username == userLogin.Username && x.Pword == userLogin.Password);

            //If valid return users token
            if (user != null)
            {
                return user.Token;
            }
            //If not valid return emtpy string
            else
            {
                return string.Empty;
            }
        }

        //Login via pin
        [HttpPost("LoginWithPin")]
        public bool LoginWithPin(string pin, string token)
        {
            var user = _educashDbContext.users.SingleOrDefault(x => x.Token ==  token && x.pin == pin);

            if (user == null)
            {
                return false;
            }

            return true;
        }

    }
}
