package com.roadrescue.auth_service.util;

import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.Set;

@Component
public class DrivingLicenseValidator {

    private static final Set<String> VALID_STATE_CODES = Set.of(
            "AP","AR","AS","BR","CG","CH","DD","DL","DN","GA","GJ",
            "HP","HR","JH","JK","KA","KL","LA","LD","MH","ML","MN",
            "MP","MZ","NL","OD","PB","PY","RJ","SK","TN","TR","TS",
            "UK","UP","WB"
    );

    private static final String DL_REGEX =
            "^[A-Z]{2}[0-9]{2}[0-9]{4}[0-9]{7}$";

    public static boolean isValidDL(String dl) {

        if (dl == null || dl.isBlank()) {
            return false;
        }

        dl = dl.toUpperCase();

        if (!dl.matches(DL_REGEX)) {
            return false;
        }

        String stateCode = dl.substring(0, 2);
        if (!VALID_STATE_CODES.contains(stateCode)) {
            return false;
        }

        int yearOfIssue = Integer.parseInt(dl.substring(4, 8));
        int currentYear = Year.now().getValue();

        if (yearOfIssue < 1980 || yearOfIssue > currentYear) {
            return false;
        }
        
        String uniqueNumber = dl.substring(8);
        if (uniqueNumber.chars().allMatch(ch -> ch == '0')) {
            return false;
        }

        return true;
    }
}

