using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using RazorPage.Models;
using RazorPage.Tools;

namespace RazorPage.Pages
{
    public class ListShopsPageModel : PageModel
    {
        // Injected dependencies
        private readonly IConfiguration _configuration;
        private readonly AddressTools _addressTools;
        private readonly CompanyTools _companyTools;

        // Constructor for dependency injection
        public ListShopsPageModel(IConfiguration configuration, AddressTools addressTools, CompanyTools companyTools)
        {
            _configuration = configuration;
            _addressTools = addressTools;
            _companyTools = companyTools;
        }

        // Properties to hold address and company data fetched from API or database
        public List<Address> Addresses { get; set; } = new();
        public List<Company> Companies { get; set; } = new();

        // Property to hold any error messages returned from API calls
        public string ApiErrorMessage { get; set; } = string.Empty;

        // Method called on HTTP GET request to load data asynchronously
        public async Task<IActionResult> OnGetAsync()
        {
            try
            {
                // Attempt to load addresses and companies data asynchronously
                Addresses = await _addressTools.GetAddressAsync();
                Companies = await _companyTools.GetCompanyAsync();
            }
            catch (HttpRequestException ex)
            {
                // Handle errors related to HTTP request issues
                ApiErrorMessage = ex.Message;
                ModelState.AddModelError(string.Empty, ApiErrorMessage);
            }
            catch (Exception ex)
            {
                // Handle any other exceptions during data loading
                ApiErrorMessage = $"Exception calling API: {ex.Message}";
                ModelState.AddModelError(string.Empty, ApiErrorMessage);
            }

            // Return the page with loaded data or error messages
            return Page();
        }
    }
}
