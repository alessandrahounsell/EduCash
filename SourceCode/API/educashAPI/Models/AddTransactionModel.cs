using System.Diagnostics.Eventing.Reader;

namespace educashAPI.Models
{
    public class AddTransactionModel
    {
        public int UserId { get; set; }
        public int CategorieId { get; set; }
        public bool Take { get; set; }
        public string Name { get; set; }
        public decimal TransactionAmmount { get; set; }
        public DateTime TransactionDate { get; set; }
    }
}
