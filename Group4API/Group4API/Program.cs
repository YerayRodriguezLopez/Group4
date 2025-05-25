// DTOs/CompanyDto.cs
namespace Group4API.DTOs
{
    public class CompanyDto
    {
        public int Id { get; set; }
        public string NIF { get; set; }
        public string Name { get; set; }
        public string Mail { get; set; }
        public int Phone { get; set; }
        public string Tags { get; set; }
        public decimal Score { get; set; }
        public bool IsProvider { get; set; }
        public bool IsRetail { get; set; }
        public AddressDto Address { get; set; }
        public List<RateDto> Rates { get; set; } = new List<RateDto>();
    }
}

// DTOs/AddressDto.cs
namespace Group4API.DTOs
{
    public class AddressDto
    {
        public int Id { get; set; }
        public string Location { get; set; }
        public float Lat { get; set; }
        public float Lng { get; set; }
        public int CompanyId { get; set; }
    }
}

// DTOs/RateDto.cs
namespace Group4API.DTOs
{
    public class RateDto
    {
        public int Id { get; set; }
        public string UserId { get; set; }
        public int CompanyId { get; set; }
        public decimal Score { get; set; }
        // Include user/company info without circular references
        public string UserEmail { get; set; }
        public string CompanyName { get; set; }
    }
}

// DTOs/UserDto.cs
namespace Group4API.DTOs
{
    public class UserDto
    {
        public string Id { get; set; }
        public string Email { get; set; }
        public string UserName { get; set; }
        public List<RateDto> Rates { get; set; } = new List<RateDto>();
    }
}