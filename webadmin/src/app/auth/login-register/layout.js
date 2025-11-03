import "../../globals.css";

export default function AuthLayout ({children}) {
    return (
        <section className="min-h-dvh grid place-items-center">
            {children}
        </section>
    );
};

