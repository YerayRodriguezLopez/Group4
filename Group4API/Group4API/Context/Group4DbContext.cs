using Microsoft.EntityFrameworkCore;
using Group4API.Model;

namespace Group4API.Context;
public class Group4DbContext : DbContext
{
    public Group4DbContext(DbContextOptions<Group4DbContext> options) : base(options) { }

    public DbSet<Address> Addresses { get; set; }
    public DbSet<Company> Companies { get; set; }
    public DbSet<Rates> Rates { get; set; }
    public DbSet<User> Users { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Company>()
            .HasMany(c => c.Rates)
            .WithOne(r => r.Company)
            .HasForeignKey(r => r.CompanyId)
            .OnDelete(DeleteBehavior.Cascade);
        modelBuilder.Entity<User>()
            .HasMany(u => u.Rates)
            .WithOne(r => r.User)
            .HasForeignKey(r => r.UserId)
            .OnDelete(DeleteBehavior.Cascade);
        modelBuilder.Entity<Address>()
            .HasOne(a => a.Company)
            .WithOne(c => c.Address)
            .HasForeignKey<Address>(a => a.CompanyId)
            .OnDelete(DeleteBehavior.Cascade);
        modelBuilder.Entity<Provider>()
            .HasMany(p => p.Companies)
            .WithMany(c => c.Providers);

        modelBuilder.Entity<Address>()
            .HasKey(a => a.Id);
        modelBuilder.Entity<User>()
            .HasKey(u => u.Id);
        modelBuilder.Entity<Company>()
            .HasKey(c => c.Id);
        modelBuilder.Entity<Rates>()
            .HasKey(c => c.Id);
    }
}
