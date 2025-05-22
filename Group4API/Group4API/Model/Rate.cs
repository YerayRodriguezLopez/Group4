using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Group4API.Model
{
    public class Rate
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int Id { get; set; }
        public string UserId { get; set; }
        public User User { get; set; }
        public int CompanyId { get; set; }
        public Company Company { get; set; }
        public decimal Score { get; set; }
    }
}
