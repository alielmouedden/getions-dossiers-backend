package ma.gov.justice.gestion_dossiers.service;

import ma.gov.justice.gestion_dossiers.entity.SystemLog;
import ma.gov.justice.gestion_dossiers.repository.SystemLogRepository;
import ma.gov.justice.gestion_dossiers.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SystemLogService {

    @Autowired
    private SystemLogRepository logRepository;

    @Autowired
    private SecurityUtils securityUtils;

    public List<SystemLog> getAllLogs() {
        return logRepository.findAllByOrderByTimestampDesc();
    }

    public void log(String username, String action, String target, String details, String type) {
        SystemLog log = new SystemLog();
        if (username == null || username.isEmpty() || "Admin".equals(username)) {
            username = securityUtils.getCurrentUsername();
        }
        log.setUsername(username);
        log.setAction(action);
        log.setTarget(target);
        log.setDetails(details);
        log.setType(type);
        log.setTimestamp(LocalDateTime.now());
        logRepository.save(log);
    }
}
