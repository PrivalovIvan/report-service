package ru.med.reportservice.report.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.med.reportservice.report.dto.ReportDataDto;
import ru.med.reportservice.report.repository.ReportRepository;

import java.util.List;

/**
 * Реализация репозитория с использованием JdbcClient.
 * Выполняет SQL-запрос с историческими JOIN-ами.
 */
@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepository {

    private final JdbcClient jdbcClient;

    @Override
    public List<ReportDataDto> getReportData() {
        return jdbcClient.sql("""
                select smo.code as smo_code,
                       smo.name as smo_name,
                       mo.code as mo_code,
                       mo.name as mo_name,
                       mkb.code as mkb_code,
                       mkb.name as mkb_name
                from med
                         join mo on mo.code = med.mo
                                        and med.date_2 between mo.dbegin and mo.dend
                         join smo on smo.code = med.cont
                                         and med.date_2 between smo.dbegin and smo.dend
                         join mkb on med.ds1 = mkb.code
                                         and med.date_2 between mkb.dbegin and mkb.dend
                order by 1,3,5
                """)
            .query(ReportDataDto.class)
            .list();
    }
}
