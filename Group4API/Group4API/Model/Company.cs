using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;
using System.Numerics;

namespace Group4API.Model
{
    public class Company
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int Id { get; set; }
        public string NIF { get; set; }
        public string Name { get; set; }
        public string Mail { get; set; }
        public int Phone { get; set; }
        public string Tags { get; set; }
        public decimal Score { get; set; }

        // Flag indicating if this company is a provider
        public bool IsProvider { get; set; }

        // Flag indicating if this company is retail
        public bool IsRetail { get; set; }

        // Collection of rates for this company
        public List<Rate> Rates { get; set; } = new List<Rate>();

        // Address relationship
        public int AddressId { get; set; }
        public Address Address { get; set; }

        // Self-referencing relationship: companies that this company provides services to
        // (only relevant if IsProvider is true)
        public List<CompanyProvider> ProvidedCompanies { get; set; } = new List<CompanyProvider>();

        // Self-referencing relationship: providers that serve this company
        public List<CompanyProvider> CompanyProviders { get; set; } = new List<CompanyProvider>();
    }
}