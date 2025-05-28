using RazorPage.Models;

namespace RazorPage.Tools
{
    public class CompanyTools
    {
        private readonly HttpClient _httpClient;

        public CompanyTools(IHttpClientFactory httpClientFactory)
        {
            _httpClient = httpClientFactory.CreateClient("AuthorizedClient");
        }

        public string ApiErrorMessage { get; set; } = string.Empty;

        public async Task<List<Company>> GetCompanyAsync()
        {
            var response = await _httpClient.GetAsync("/api/Companies");
            if (response.IsSuccessStatusCode)
            {
                var companies = await response.Content.ReadFromJsonAsync<List<Company>>();
                return companies?
                    .OrderByDescending(g => g.Id)
                    .ToList() ?? new List<Company>();
            }
            else
            {
                var body = await response.Content.ReadAsStringAsync();
                throw new HttpRequestException($"API Error ({response.StatusCode}): {body}");
            }
        }

        public async Task<Company> GetCompanyByIdAsync(int companyId)
        {
            var response = await _httpClient.GetAsync($"/api/Companies/{companyId}");
            if (response.IsSuccessStatusCode)
            {
                var company = await response.Content.ReadFromJsonAsync<Company>();
                return company;
            }
            else
            {
                var body = await response.Content.ReadAsStringAsync();
                throw new HttpRequestException($"API Error ({response.StatusCode}): {body}");
            }
        }

        
    }
}
