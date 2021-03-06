package ru.geekbrains.controller;


import ru.geekbrains.persist.Company;
import ru.geekbrains.persist.CompanyRepository;
import ru.geekbrains.persist.Product;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

@SessionScoped
@Named
public class CompanyController implements Serializable {

    @Inject
    private CompanyRepository companyRepository;

    private Company company;

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Company> getAllCompanies() throws SQLException {
        return companyRepository.findAll();
    }

    public String editCompany(Company company) {
        this.company = company;
        return "/about.xhtml?faces-redirect=true";
    }

    public void deleteCompany(Company company) throws SQLException {
        companyRepository.delete(company.getId());
    }

    public String createCompany() {
        this.company = new Company();
        return "/about.xhtml?faces-redirect=true";
    }

    public String saveCompany() throws SQLException {
        if (company.getId() != null) {
            companyRepository.update(company);
        } else {
            companyRepository.insert(company);
        }
        return "/about.xhtml?faces-redirect=true";
    }

}
