package policy;

import main.Action;
import main.State;

import java.io.Serializable;

public interface Policy {
	Action getAction(State state);
}
