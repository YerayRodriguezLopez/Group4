using RazorPage.Models;

namespace RazorPage.Tools
{
    public class AddressTools
    {
        private readonly HttpClient _httpClient;

        public AddressTools(IHttpClientFactory httpClientFactory)
        {
            _httpClient = httpClientFactory.CreateClient("AuthorizedClient");
        }

        public string ApiErrorMessage { get; set; } = string.Empty;

        public async Task<List<Address>> GetAddressAsync()
        {
            var response = await _httpClient.GetAsync("/api/Addresses");
            if (response.IsSuccessStatusCode)
            {
                var addresses = await response.Content.ReadFromJsonAsync<List<Address>>();
                return addresses?
                    .OrderByDescending(g => g.Id)
                    .ToList() ?? new List<Address>();
            }
            else
            {
                var body = await response.Content.ReadAsStringAsync();
                throw new HttpRequestException($"API Error ({response.StatusCode}): {body}");
            }
        }

        public async Task<Address> GetAddressByIdAsync(int addressId)
        {
            var response = await _httpClient.GetAsync($"/api/Addresses/{addressId}");
            if (response.IsSuccessStatusCode)
            {
                var address = await response.Content.ReadFromJsonAsync<Address>();
                return address;
            }
            else
            {
                var body = await response.Content.ReadAsStringAsync();
                throw new HttpRequestException($"API Error ({response.StatusCode}): {body}");
            }
        }
    }
}
