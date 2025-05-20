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
        public bool IsProvider { get; set; }
        public bool IsRetail { get; set; }
        public List<Provider> Providers { get; set; } = new List<Provider>();
        public List<Rates> Rates { get; set; } = new List<Rates>();
        public int AddressId { get; set; }
        public Address Address { get; set; }
    }
}
