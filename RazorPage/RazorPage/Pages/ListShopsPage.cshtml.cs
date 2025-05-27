using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using RazorPage.Models;
using RazorPage.Tools;

namespace RazorPage.Pages
{
    public class ListShopsPageModel : PageModel
    {
        private readonly IConfiguration _configuration;
        private readonly AddressTools _addressTools;
        private readonly CompanyTools _companyTools;

        public ListShopsPageModel(IConfiguration configuration, AddressTools addressTools, CompanyTools companyTools)
        {
            _configuration = configuration;
            _addressTools = addressTools;
            _companyTools = companyTools;
        }

        public List<Address> Addresses { get; set; } = new();

        public List<Company> Companies { get; set; } = new();

        public string ApiErrorMessage { get; set; } = string.Empty;

        public async Task<IActionResult> OnGetAsync()
        {

            try
            {
                Addresses = await _addressTools.GetAddressAsync();
                Companies = await _companyTools.GetCompanyAsync();
            }
            catch (HttpRequestException ex)
            {
                ApiErrorMessage = ex.Message;
                ModelState.AddModelError(string.Empty, ApiErrorMessage);
            }
            catch (Exception ex)
            {
                ApiErrorMessage = $"Exception calling API: {ex.Message}";
                ModelState.AddModelError(string.Empty, ApiErrorMessage);
            }

            return Page();
        }
    }
}
