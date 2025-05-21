namespace Group4API.Model
{
    public class CompanyProvider
    {
        public int ProviderId { get; set; }
        public Company Provider { get; set; }

        public int CompanyId { get; set; }
        public Company Company { get; set; }
    }
}
