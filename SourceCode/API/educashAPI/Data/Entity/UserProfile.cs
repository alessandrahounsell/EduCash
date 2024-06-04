using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace educashAPI.Data.Entity
{
    [Table("UserProfile")]
    public class UserProfile
    {
        [Key]
        [JsonIgnore]
        public int UserID { get; set; }
        public string Username { get; set; }
        [JsonIgnore]
        public string Pword { get; set; }
        [JsonIgnore]
        public string pin { get; set; }
        [JsonIgnore]
        public string Token { get; set; }
        public bool LIP { get; set; }
        public bool negAllowed { get; set; }
    }
}
