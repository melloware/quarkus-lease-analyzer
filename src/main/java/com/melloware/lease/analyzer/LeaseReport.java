package com.melloware.lease.analyzer;

import java.math.BigDecimal;
import java.time.LocalDate;
/**
 * Record representing a lease report containing key information extracted from a lease agreement.
 *
 * @param agreementDate The date when the lease agreement was signed
 * @param termStartDate The date when the lease term begins
 * @param termEndDate The date when the lease term ends
 * @param developmentTermEndDate The end date of any development/construction period
 * @param landlordName The name of the landlord/property owner
 * @param tenantName The name of the tenant/lessee
 * @param acres The size of the leased property in acres
 */
public record LeaseReport(
        LocalDate agreementDate,
        LocalDate termStartDate,
        LocalDate termEndDate,
        LocalDate developmentTermEndDate,
        String landlordName,
        String tenantName,
        BigDecimal acres) {
}