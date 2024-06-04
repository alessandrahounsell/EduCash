using System.ComponentModel.DataAnnotations;

namespace educashAPI.Models
{
    public class CreateUserModel
    {
        public string Username { get; set; }
        public string Pword { get; set; }
        public string pin { get; set; }
        public bool LIP { get; set; }
    }
}
