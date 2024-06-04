using Microsoft.Identity.Client;
using System.ComponentModel.DataAnnotations;

namespace educashAPI.Models
{
    public class UpdateUserModel : CreateUserModel
    {
        [Required]
        public string UserToken { get; set; }
    }
}
