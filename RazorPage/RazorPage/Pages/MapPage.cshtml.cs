using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using RazorPage.Models;
using RazorPage.Tools;

namespace RazorPage.Pages
{
    public class MapPageModel : PageModel
    {
        public int TotalCompanies { get; set; }
        public int ProvidersCount { get; set; }
        public int NonProvidersCount { get; set; }
        public int RetailCount { get; set; }
        public int NonRetailCount { get; set; }
        public int OnlyProviderCount { get; set; }
        public int OnlyRetailCount { get; set; }
        public int BothCount { get; set; }
        public int NeitherCount { get; set; }


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
            CalculateStatistics(Companies);
            return Page();
        }
        private void CalculateStatistics(List<Company> companies)
        {
            TotalCompanies = companies.Count;

            // Contar por cada atributo individual
            ProvidersCount = companies.Count(c => c.IsProvider);
            NonProvidersCount = companies.Count(c => !c.IsProvider);
            RetailCount = companies.Count(c => c.isRetail);
            NonRetailCount = companies.Count(c => !c.isRetail);

            // Contar combinaciones
            OnlyProviderCount = companies.Count(c => c.IsProvider && !c.isRetail);
            OnlyRetailCount = companies.Count(c => !c.IsProvider && c.isRetail);
            BothCount = companies.Count(c => c.IsProvider && c.isRetail);
            NeitherCount = companies.Count(c => !c.IsProvider && !c.isRetail);
        }
    }
}
