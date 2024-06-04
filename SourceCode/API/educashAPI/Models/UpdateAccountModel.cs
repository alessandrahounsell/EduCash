using System.ComponentModel.DataAnnotations;

namespace educashAPI.Models
{
    public class UpdateAccountModel : NewAccountsModel
    {
        [Required]
        public int FinancesID { get; set;}
    }
}
