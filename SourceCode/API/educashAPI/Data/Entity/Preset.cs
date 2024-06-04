using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace educashAPI.Data.Entity
{
    [Table("Preset")]
    public class Preset
    {
        [Key]
        public int PresetId { get; set; }
        [JsonIgnore]
        public int? userId { get; set; }
        public string PresetName { get; set; }
        public decimal Price { get; set; }
    }
}
