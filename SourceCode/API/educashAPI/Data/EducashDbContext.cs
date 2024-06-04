using educashAPI.Data.Entity;
using Microsoft.EntityFrameworkCore;

namespace educashAPI.Data
{
    public class EducashDbContext : DbContext
    {
        public EducashDbContext(DbContextOptions<EducashDbContext> Options) : base(Options)
        {

        }

        public DbSet<UserProfile> users { get; set; }
        public DbSet<TransactionTable> transactions { get; set; }
        public DbSet<AccountTotals> accounts { get; set; }
        public DbSet<Categories> categories { get; set; }
        public DbSet<Preset> presets { get; set; }

        //Set up one to many relationship between transaction and categorie
        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<TransactionTable>()
                .HasOne(e => e.Categorie)
                .WithMany(e => e.Transactions)
                .HasForeignKey(e => e.Categorieid);
        }
    }
}
