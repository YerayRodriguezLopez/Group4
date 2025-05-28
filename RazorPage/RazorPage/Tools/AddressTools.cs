using RazorPage.Models;

namespace RazorPage.Tools
{
    public class AddressTools
    {
        // HttpClient instance used to make API requests
        private readonly HttpClient _httpClient;

        // Constructor that initializes HttpClient using a named client from IHttpClientFactory
        public AddressTools(IHttpClientFactory httpClientFactory)
        {
            _httpClient = httpClientFactory.CreateClient("AuthorizedClient");
        }

        // Property to hold any API error messages
        public string ApiErrorMessage { get; set; } = string.Empty;

        /// <summary>
        /// Retrieves the list of addresses asynchronously from the API.
        /// </summary>
        /// <returns>A list of Address objects sorted by descending Id.</returns>
        public async Task<List<Address>> GetAddressAsync()
        {
            var response = await _httpClient.GetAsync("/api/Addresses");

            if (response.IsSuccessStatusCode)
            {
                // Deserialize the JSON response to a list of Address objects
                var addresses = await response.Content.ReadFromJsonAsync<List<Address>>();

                // Return the list ordered by descending Id, or empty list if null
                return addresses?
                    .OrderByDescending(g => g.Id)
                    .ToList() ?? new List<Address>();
            }
            else
            {
                // Read error response body and throw an exception with details
                var body = await response.Content.ReadAsStringAsync();
                throw new HttpRequestException($"API Error ({response.StatusCode}): {body}");
            }
        }

        /// <summary>
        /// Retrieves a single address by its ID asynchronously from the API.
        /// </summary>
        /// <param name="addressId">The ID of the address to fetch.</param>
        /// <returns>The Address object with the specified ID.</returns>
        public async Task<Address> GetAddressByIdAsync(int addressId)
        {
            var response = await _httpClient.GetAsync($"/api/Addresses/{addressId}");

            if (response.IsSuccessStatusCode)
            {
                // Deserialize the JSON response to a single Address object
                var address = await response.Content.ReadFromJsonAsync<Address>();
                return address;
            }
            else
            {
                // Read error response body and throw an exception with details
                var body = await response.Content.ReadAsStringAsync();
                throw new HttpRequestException($"API Error ({response.StatusCode}): {body}");
            }
        }
    }
}
