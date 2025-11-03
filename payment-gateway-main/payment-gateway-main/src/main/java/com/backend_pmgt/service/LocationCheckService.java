package com.backend_pmgt.service;

import com.backend_pmgt.dto.rules.RulesLogicResultDTO;
import com.backend_pmgt.entity.RulesValue;
import com.backend_pmgt.entity.Transaction;
import com.backend_pmgt.repository.RulesValueRepository;
import com.backend_pmgt.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LocationCheckService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private RulesValueRepository rulesValueRepository;

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371; // Radius of the earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    public RulesLogicResultDTO check() {
        // Fetch the latest two transactions ordered by date descending
        RulesValue rulesValue = rulesValueRepository.getReferenceById(Long.valueOf(1));
        double distanceThreshold = rulesValue.getRulesAmountLimit();
        int timeRange = rulesValue.getRulesTimeRange();
        Optional<Transaction> latestTransactionOpt = transactionRepository.findLatestTransaction();
        Optional<Transaction> previousTransactionOpt = transactionRepository.findPreviousTransaction();

        RulesLogicResultDTO resultDTO = new RulesLogicResultDTO();
        resultDTO.setFraudType(1);
        resultDTO.setFraudID(1);

        if (latestTransactionOpt.isEmpty() || previousTransactionOpt.isEmpty()) {
            // Not enough transactions to compare, consider safe
            resultDTO.setFlagResult(false);
            return resultDTO;
        }

        Transaction latestTransaction = latestTransactionOpt.get();
        Transaction previousTransaction = previousTransactionOpt.get();

        String latestLocation = latestTransaction.getTrxLocation();
        String previousLocation = previousTransaction.getTrxLocation();

        if (latestLocation == null || previousLocation == null) {
            // Missing location data, consider safe
            resultDTO.setFlagResult(false);
            return resultDTO;
        }

        String[] latestCoords = latestLocation.split(",");
        String[] previousCoords = previousLocation.split(",");

        if (latestCoords.length != 2 || previousCoords.length != 2) {
            // Invalid location format, consider safe
            resultDTO.setFlagResult(false);
            return resultDTO;
        }

        try {
            double latestLat = Double.parseDouble(latestCoords[0].trim());
            double latestLon = Double.parseDouble(latestCoords[1].trim());
            double previousLat = Double.parseDouble(previousCoords[0].trim());
            double previousLon = Double.parseDouble(previousCoords[1].trim());

            double distance = calculateDistance(latestLat, latestLon, previousLat, previousLon);

            // Check the time range
            long timeDifferenceMillis = latestTransaction.getTrxDate().getTime() - previousTransaction.getTrxDate().getTime();
            long timeDifferenceMinutes = timeDifferenceMillis / (1000 * 60);
            boolean isWithinTimeRange = Math.abs(timeDifferenceMinutes) <= timeRange;

            boolean result = distance > distanceThreshold && isWithinTimeRange;
            resultDTO.setFlagResult(result);
            return resultDTO;

        } catch (NumberFormatException e) {
            // Parsing error, consider safe
            resultDTO.setFlagResult(false);
            return resultDTO;
        }
    }
}
