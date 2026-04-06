package calixy.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WaterLogRequest {

    @NotNull(message = "Amount is required")
    @Min(value = 50, message = "Minimum amount is 50ml")
    @Max(value = 2000, message = "Maximum amount is 2000ml per entry")
    private Integer amountMl;
}