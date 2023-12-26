package mk.ukim.finki.wp.kol2022.g1.repository;

import mk.ukim.finki.wp.kol2022.g1.model.Employee;
import mk.ukim.finki.wp.kol2022.g1.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    List<Employee> findByEmploymentDateBeforeAndSkillsContaining(Skill skill, LocalDate date);
    List<Employee>findByEmploymentDateBefore(LocalDate date);
    List<Employee>findBySkillsContaining(Skill skill);
}
