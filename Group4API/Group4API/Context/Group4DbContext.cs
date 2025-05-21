using Microsoft.EntityFrameworkCore;
using Group4API.Model;

namespace Group4API.Context;
public class Group4DbContext : DbContext
{
    public Group4DbContext(DbContextOptions<Group4DbContext> options) : base(options) { }

    public DbSet<Address> Addresses { get; set; }
    public DbSet<Company> Companies { get; set; }
    public DbSet<Rate> Rates { get; set; }
    public DbSet<User> Users { get; set; }
    public DbSet<CompanyProvider> CompanyProviders { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        // Configure Company-Rate relationship
        modelBuilder.Entity<Company>()
            .HasMany(c => c.Rates)
            .WithOne(r => r.Company)
            .HasForeignKey(r => r.CompanyId)
            .OnDelete(DeleteBehavior.Cascade);

        // Configure User-Rate relationship
        modelBuilder.Entity<User>()
            .HasMany(u => u.Rates)
            .WithOne(r => r.User)
            .HasForeignKey(r => r.UserId)
            .OnDelete(DeleteBehavior.Cascade);

        // Configure Company-Address relationship
        modelBuilder.Entity<Address>()
            .HasOne(a => a.Company)
            .WithOne(c => c.Address)
            .HasForeignKey<Address>(a => a.CompanyId)
            .OnDelete(DeleteBehavior.Cascade);

        // Configure CompanyProvider (self-referencing relationship)
        modelBuilder.Entity<CompanyProvider>()
            .HasKey(cp => new { cp.ProviderId, cp.CompanyId });

        modelBuilder.Entity<CompanyProvider>()
            .HasOne(cp => cp.Provider)
            .WithMany(c => c.ProvidedCompanies)
            .HasForeignKey(cp => cp.ProviderId)
            .OnDelete(DeleteBehavior.Restrict); // Use Restrict to avoid circular cascade delete

        modelBuilder.Entity<CompanyProvider>()
            .HasOne(cp => cp.Company)
            .WithMany(c => c.CompanyProviders)
            .HasForeignKey(cp => cp.CompanyId)
            .OnDelete(DeleteBehavior.Restrict); // Use Restrict to avoid circular cascade delete

        // Define primary keys
        modelBuilder.Entity<Address>()
            .HasKey(a => a.Id);
        modelBuilder.Entity<User>()
            .HasKey(u => u.Id);
        modelBuilder.Entity<Company>()
            .HasKey(c => c.Id);
        modelBuilder.Entity<Rate>()
            .HasKey(c => c.Id);
    }
}
