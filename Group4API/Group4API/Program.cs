using Microsoft.EntityFrameworkCore;
using Group4API.Context;
using Microsoft.OpenApi.Models;
using Group4API.Model;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;

namespace Group4API
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            // Add services to the container.  
            builder.Services.AddControllers();

            // Add database context
            builder.Services.AddDbContext<Group4DbContext>(options =>
                options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection")));

            // Add Identity services
            builder.Services.AddIdentity<User, IdentityRole>(options =>
            {
                // Configure Identity options if needed
                options.Password.RequireDigit = true;
                options.Password.RequireLowercase = true;
                options.Password.RequireUppercase = true;
                options.Password.RequireNonAlphanumeric = true;
                options.Password.RequiredLength = 6;
            })
                .AddEntityFrameworkStores<Group4DbContext>() // Ensure the correct namespace is included
                .AddDefaultTokenProviders();

            // Add Swagger services  
            builder.Services.AddEndpointsApiExplorer();
            builder.Services.AddSwaggerGen(c =>
            {
                c.SwaggerDoc("v1", new OpenApiInfo
                {
                    Title = "Group4API",
                    Version = "v1"
                });
            });

            var app = builder.Build();

            // Configure the HTTP request pipeline.  
            if (app.Environment.IsDevelopment())
            {
                app.UseSwagger();
                app.UseSwaggerUI(c =>
                {
                    c.SwaggerEndpoint("/swagger/v1/swagger.json", "Group4API v1");
                });
            }

            app.UseHttpsRedirection();

            // Add authentication middleware
            app.UseAuthentication();
            app.UseAuthorization();

            app.MapControllers();

            app.Run();
        }
    }
}
