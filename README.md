# Salary Calculator (Ukraine & Portugal)

A modern, dynamic salary calculator comparing tax regimes and net income between Ukraine and Portugal.

🚀 **Live Frontend Demo:** [https://Ro-Ly.github.io/salary-calculator/](https://Ro-Ly.github.io/salary-calculator/)

## Features
- **Ukraine:** Calculates net income for standard Employment and B2B (3rd group 5% + ESV).
- **Portugal:** Calculates progressive IRS and Social Security for Employment, handles the simplified regime for B2B, and includes an option for the NHR (Non-Habitual Resident) 20% flat tax rate.
- **Dynamic Conversion:** All calculations use EUR as the base currency and are automatically displayed alongside their USD equivalents.
- **Instant Recalculation:** Enter a number into any field (Annual Gross, Monthly Net, Hourly rate, etc.) and all other fields auto-fill instantly.
- **Premium Design:** Brutalist, high-contrast dark mode aesthetic with smooth micro-interactions.

## Tech Stack
- **Backend:** Java 21, Spring Boot (REST API), designed for Docker deployment on Render.
- **Frontend:** Angular 17, SCSS, deployed to GitHub Pages.

## Local Development
1. **Start Backend:** 
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
   *Runs on port 8080.*

2. **Start Frontend:**
   ```bash
   cd frontend
   npm install
   npm start
   ```
   *Runs on port 4200.*
