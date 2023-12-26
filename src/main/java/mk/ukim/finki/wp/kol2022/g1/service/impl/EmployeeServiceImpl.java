package mk.ukim.finki.wp.kol2022.g1.service.impl;

import mk.ukim.finki.wp.kol2022.g1.model.Employee;
import mk.ukim.finki.wp.kol2022.g1.model.EmployeeType;
import mk.ukim.finki.wp.kol2022.g1.model.Skill;
import mk.ukim.finki.wp.kol2022.g1.model.exceptions.InvalidEmployeeIdException;
import mk.ukim.finki.wp.kol2022.g1.repository.EmployeeRepository;
import mk.ukim.finki.wp.kol2022.g1.repository.SkillRepository;
import mk.ukim.finki.wp.kol2022.g1.service.EmployeeService;
import mk.ukim.finki.wp.kol2022.g1.service.SkillService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final SkillRepository skillRepository;
    private final SkillService skillService;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, SkillRepository skillRepository, SkillService skillService, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.skillRepository = skillRepository;
        this.skillService = skillService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Employee> listAll() {
        return this.employeeRepository.findAll();
    }

    @Override
    public Employee findById(Long id) {
        return this.employeeRepository.findById(id).orElseThrow(InvalidEmployeeIdException::new);
    }

    @Override
    public Employee create(String name, String email, String password, EmployeeType type, List<Long> skillId, LocalDate employmentDate) {
        //List<Skill> skills=this.skillRepository.findAllById(skillId);
        return this.employeeRepository.save(new Employee(name,email,passwordEncoder.encode(password),type,
                skillId.stream().map(id -> this.skillService.findById(id)).collect(Collectors.toList()),
                employmentDate));
    }

    @Override
    public Employee update(Long id, String name, String email, String password, EmployeeType type, List<Long> skillId, LocalDate employmentDate) {
        Employee temp=this.employeeRepository.findById(id).orElseThrow(InvalidEmployeeIdException::new);
        temp.setName(name);
        temp.setEmail(email);
        temp.setPassword(passwordEncoder.encode(password));
        temp.setType(type);
        temp.setEmploymentDate(employmentDate);
        //VO CREATE I TUKA KE TREBA PREKU FINDID DA GO ZEMES SKILLOT PA POTOA DA GO STAVIS ZA SEKOJ EMPLOYEE,NAJVEROJATNO SO STREAM
        temp.setSkills(skillId.stream().map(i -> this.skillService.findById(i)).collect(Collectors.toList()));
        return this.employeeRepository.save(temp);
    }

    @Override
    public Employee delete(Long id) {
        Employee temp=this.employeeRepository.findById(id).orElseThrow(InvalidEmployeeIdException::new);
        this.employeeRepository.delete(temp);
        return temp;
    }

    @Override
    public List<Employee> filter(Long skillId, Integer yearsOfService) {
        if (skillId != null && yearsOfService != null) {
            Skill skill = skillService.findById(skillId);
            LocalDate date = LocalDate.now().minusYears(yearsOfService);
            return employeeRepository.findByEmploymentDateBeforeAndSkillsContaining(skill, date);
        } else if (skillId == null && yearsOfService == null) {
           return this.employeeRepository.findAll();
        } else if (skillId != null) {
            Skill skill = skillService.findById(skillId);
            return employeeRepository.findBySkillsContaining(skill);
        } else {
            LocalDate date = LocalDate.now().minusYears(yearsOfService);
            return employeeRepository.findByEmploymentDateBefore(date);
        }
    }
}
