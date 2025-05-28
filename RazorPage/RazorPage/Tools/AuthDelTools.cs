using System.Net.Http.Headers;

namespace RazorPage.Tools
{
    public class AuthDelTools : DelegatingHandler
    {
        // IHttpContextAccessor to access the current HTTP context, including request cookies
        private readonly IHttpContextAccessor _httpContextAccessor;

        // Constructor to inject IHttpContextAccessor dependency
        public AuthDelTools(IHttpContextAccessor httpContextAccessor)
        {
            _httpContextAccessor = httpContextAccessor;
        }

        /// <summary>
        /// Overrides SendAsync to add the Bearer token from cookies to the Authorization header of outgoing HTTP requests.
        /// </summary>
        /// <param name="request">The outgoing HTTP request message.</param>
        /// <param name="cancellationToken">Cancellation token.</param>
        /// <returns>The HTTP response message from the base SendAsync method.</returns>
        protected override async Task<HttpResponseMessage> SendAsync(HttpRequestMessage request, CancellationToken cancellationToken)
        {
            // Retrieve the "authToken" cookie from the current HTTP request
            var token = _httpContextAccessor.HttpContext.Request.Cookies["authToken"];

            // If a token exists, add it as a Bearer token in the Authorization header
            if (!string.IsNullOrEmpty(token))
            {
                request.Headers.Authorization = new AuthenticationHeaderValue("Bearer", token);
            }

            // Continue processing the HTTP request pipeline
            return await base.SendAsync(request, cancellationToken);
        }
    }
}
