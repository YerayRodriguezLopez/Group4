using Microsoft.EntityFrameworkCore;
using Group4API.Context;

namespace Group4API.Tests.TestHelpers
{
    public static class InMemoryDbContextFactory
    {
        public static Group4DbContext Create()
        {
            var options = new DbContextOptionsBuilder<Group4DbContext>()
                .UseInMemoryDatabase(databaseName: Guid.NewGuid().ToString())
                .Options;

            return new Group4DbContext(options);
        }
    }
}