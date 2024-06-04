using Microsoft.EntityFrameworkCore.Storage;
using System.ComponentModel;
using System.Text.Json.Serialization;

namespace educashAPI.Models
{
    public class StatsReturnModel
    {
        public string CatName { get; set; }
        [JsonIgnore]
        public int catId { get; set; }
        public decimal CatTotal { get; set; }
        public decimal Percentage { get; set; }
    }
}
