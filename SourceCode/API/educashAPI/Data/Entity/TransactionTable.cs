using Microsoft.Identity.Client;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace educashAPI.Data.Entity
{
    [Table("Transactions")]
    public class TransactionTable
    {
        [Key]
        public int TransactionId { get; set; }
        [JsonIgnore]
        public int UserId { get; set; }
        [JsonIgnore]
        public int Categorieid { get; set; }
        public Categories Categorie { get; set; }
        public bool Take { get; set; } 
        public string? Name { get; set; }
        public decimal TransactionAmount { get; set; }
        public DateTime TransactionDate { get; set; }
    }
}
