package univ.lille.module_maintenance.component;

@Data
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "with")
public class TicketCatalog {
    private final Set<Ticket> tickets;

    public static TicketCatalog EMPTY = TicketCatalog.with(Collections.emptySet());
}