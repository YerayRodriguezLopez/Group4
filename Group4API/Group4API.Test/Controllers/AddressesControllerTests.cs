using Microsoft.AspNetCore.Mvc;
using Group4API.Controllers;
using Group4API.Model;
using Group4API.Tests.TestHelpers;
using Xunit;
using Group4API.Context;

namespace Group4API.Tests.Controllers
{
    public class AddressesControllerTests : IDisposable
    {
        private readonly Group4DbContext _context;
        private readonly AddressesController _controller;

        public AddressesControllerTests()
        {
            _context = InMemoryDbContextFactory.Create();
            _controller = new AddressesController(_context);
            SeedData();
        }

        private void SeedData()
        {
            var company = new Company
            {
                Id = 1,
                Name = "Test Company",
                NIF = "12345678A",
                Mail = "test@company.com",
                Phone = 123456789,
                Tags = "test",
                Score = 4.5m,
                IsProvider = true,
                IsRetail = false
            };

            var address = new Address
            {
                Id = 1,
                Location = "Test Location",
                Lat = 41.123f,
                Lng = 2.456f,
                CompanyId = 1,
                Company = company
            };

            _context.Companies.Add(company);
            _context.Addresses.Add(address);
            _context.SaveChanges();
        }

        [Fact]
        public async Task GetAddresses_ReturnsAllAddresses()
        {
            // Act
            var result = await _controller.GetAddresses();

            // Assert
            var okResult = Assert.IsType<ActionResult<IEnumerable<Address>>>(result);
            var addresses = Assert.IsAssignableFrom<IEnumerable<Address>>(okResult.Value);
            Assert.Single(addresses);
        }

        [Fact]
        public async Task GetAddress_WithValidId_ReturnsAddress()
        {
            // Act
            var result = await _controller.GetAddress(1);

            // Assert
            var okResult = Assert.IsType<ActionResult<Address>>(result);
            var address = Assert.IsType<Address>(okResult.Value);
            Assert.Equal(1, address.Id);
        }

        [Fact]
        public async Task GetAddress_WithInvalidId_ReturnsNotFound()
        {
            // Act
            var result = await _controller.GetAddress(999);

            // Assert
            Assert.IsType<NotFoundResult>(result.Result);
        }

        [Fact]
        public async Task PostAddress_WithValidData_CreatesAddress()
        {
            // Arrange
            var newAddress = new Address
            {
                Location = "New Location",
                Lat = 42.123f,
                Lng = 3.456f,
                CompanyId = 1
            };

            // Act
            var result = await _controller.PostAddress(newAddress);

            // Assert
            var createdResult = Assert.IsType<CreatedAtActionResult>(result.Result);
            var address = Assert.IsType<Address>(createdResult.Value);
            Assert.Equal("New Location", address.Location);
        }

        [Fact]
        public async Task PostAddress_WithInvalidCompanyId_ReturnsBadRequest()
        {
            // Arrange
            var newAddress = new Address
            {
                Location = "New Location",
                Lat = 42.123f,
                Lng = 3.456f,
                CompanyId = 999
            };

            // Act
            var result = await _controller.PostAddress(newAddress);

            // Assert
            Assert.IsType<BadRequestObjectResult>(result.Result);
        }

        [Fact]
        public async Task DeleteAddress_WithValidId_DeletesAddress()
        {
            // Act
            var result = await _controller.DeleteAddress(1);

            // Assert
            Assert.IsType<NoContentResult>(result);
            var deletedAddress = await _context.Addresses.FindAsync(1);
            Assert.Null(deletedAddress);
        }

        public void Dispose()
        {
            _context.Dispose();
        }
    }
}