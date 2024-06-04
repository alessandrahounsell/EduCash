using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Diagnostics;
using System.Reflection;
using System.Text.Json.Serialization;

namespace educashAPI.Data.Entity
{
    [Table("Categories")]
    public class Categories
    {
        [Key]
        public int CategorieId { get; set; }
        [JsonIgnore]
        public int? UserId { get; set; }
        public string CategorieName { get; set; }
        [JsonIgnore]
        public List<TransactionTable> Transactions { get; set; }


    }
}
