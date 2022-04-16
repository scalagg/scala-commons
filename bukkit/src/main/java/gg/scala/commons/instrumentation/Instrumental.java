package gg.scala.commons.instrumentation;

import java.lang.instrument.Instrumentation;
import java.util.Properties;
import java.util.UUID;

/**
 * @author GrowlyX
 * @since 4/15/2022
 */
public class Instrumental
{
    private static final Object AGENT_ARGS_KEY = UUID.randomUUID();
    private static final Object INSTRUMENTATION_KEY = UUID.randomUUID();

    public static void premain(
            String agentArgs, Instrumentation instrumentation
    )
    {
        final Properties properties =
                System.getProperties();

        if (agentArgs != null)
        {
            properties.put(AGENT_ARGS_KEY, agentArgs);
        }

        properties.put(
                INSTRUMENTATION_KEY, instrumentation
        );
    }

    public static Instrumentation instrumentation() {
        return (Instrumentation) System.getProperties().get(INSTRUMENTATION_KEY);
    }
}
