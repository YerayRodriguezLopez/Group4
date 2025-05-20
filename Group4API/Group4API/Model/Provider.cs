namespace Group4API.Model
{
    public class Provider
    {
        public int Id { get; set; }
        public List<Company> Companies { get; set; } = new List<Company>();
    }
}
