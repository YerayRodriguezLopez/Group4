using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using RazorPage.Models;
using RazorPage.Tools;

namespace RazorPage.Pages
{
    public class MapPageModel : PageModel
    {
        public readonly AddressTools _addressTools;
        private readonly CompanyTools _companyTools;

        public MapPageModel(AddressTools addressTools, CompanyTools companyTools)
        {
            _addressTools = addressTools;
            _companyTools = companyTools;
        }

        public List<Company> Companies { get; set; } = new();
        public List<Address> Addresses { get; set; } = new();

        public async Task<IActionResult> OnGetAsync()
        {
            Addresses = await _addressTools.GetAddressAsync();
            Companies = await _companyTools.GetCompanyAsync();
            return Page();
        }
    }
}
