package jellyqueen.rescat.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDto {

    @ApiModelProperty(example = "ttmom96", position = 1)
    private String id;
    @ApiModelProperty(example = "ttmom1234", position = 2)
    private String password;
    @ApiModelProperty(example = "InstanceToken", position = 3)
    private String instanceToken;
}





