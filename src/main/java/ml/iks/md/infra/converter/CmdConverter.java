package ml.iks.md.infra.converter;
import ml.iks.md.models.Command;
import ml.iks.md.repositories.CommandRepository;
import ml.iks.md.util.BeanLocator;
import org.springframework.util.StringUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.Optional;

/**
 * ThemeConverter
 *
 * @author  Oleg Varaksin / last modified by $Author$
 * @version $Revision$
 */
@FacesConverter("cmdConverter")
public class CmdConverter implements Converter {

    public Command getAsObject(final FacesContext context, final UIComponent component, final String value) {
        Optional<Command> cmd = BeanLocator.find(CommandRepository.class).findById(Long.valueOf(value));
        if(!cmd.isPresent())
            return null;
        return cmd.get();
    }

    public String getAsString(final FacesContext context, final UIComponent component, final Object value) {
        if(StringUtils.isEmpty(value))
            return "";
        return value + "";
    }
}
