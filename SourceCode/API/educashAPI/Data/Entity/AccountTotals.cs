using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace educashAPI.Data.Entity
{
    [Table("AccountTotals")]
    public class AccountTotals
    {
        [Key]
        public int FinancesID { get; set; }
        [JsonIgnore]
        public int UserId { get; set; }
        public decimal CurrentAmount { get; set; }
        public decimal Savings { get; set; }
    }
}
