using RazorPage.Models;

namespace RazorPage.Tools
{
    public class CompanyTools
    {
        private readonly HttpClient _httpClient;

        // Constructor to initialize HttpClient using an IHttpClientFactory with a named client
        public CompanyTools(IHttpClientFactory httpClientFactory)
        {
            _httpClient = httpClientFactory.CreateClient("AuthorizedClient");
        }

        // Property to store error messages from API calls
        public string ApiErrorMessage { get; set; } = string.Empty;

        /// <summary>
        /// Retrieves the full list of companies from the API.
        /// </summary>
        /// <returns>A list of Company objects, ordered by Id descending.</returns>
        /// <exception cref="HttpRequestException">Thrown when API call fails.</exception>
        public async Task<List<Company>> GetCompanyAsync()
        {
            // Send GET request to the API endpoint for companies
            var response = await _httpClient.GetAsync("/api/Companies");
            if (response.IsSuccessStatusCode)
            {
                // Parse the response content as a list of Company objects
                var companies = await response.Content.ReadFromJsonAsync<List<Company>>();

                // Return the companies ordered by descending Id or an empty list if null
                return companies?
                    .OrderByDescending(g => g.Id)
                    .ToList() ?? new List<Company>();
            }
            else
            {
                // If API response is not successful, read the error body and throw exception
                var body = await response.Content.ReadAsStringAsync();
                throw new HttpRequestException($"API Error ({response.StatusCode}): {body}");
            }
        }

        /// <summary>
        /// Retrieves a specific company by its Id from the API.
        /// </summary>
        /// <param name="companyId">The unique identifier of the company to fetch.</param>
        /// <returns>A Company object if found.</returns>
        /// <exception cref="HttpRequestException">Thrown when API call fails.</exception>
        public async Task<Company> GetCompanyByIdAsync(int companyId)
        {
            // Send GET request for a single company by its Id
            var response = await _httpClient.GetAsync($"/api/Companies/{companyId}");
            if (response.IsSuccessStatusCode)
            {
                // Parse the response content as a Company object
                var company = await response.Content.ReadFromJsonAsync<Company>();
                return company;
            }
            else
            {
                // If API response is not successful, read the error body and throw exception
                var body = await response.Content.ReadAsStringAsync();
                throw new HttpRequestException($"API Error ({response.StatusCode}): {body}");
            }
        }
    }
}
