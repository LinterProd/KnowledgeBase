import React from "react";
import Link from "next/link";

interface NavItemProps {
    icon: React.ReactNode | string;
    label: string;
    href?: string; // Делаем необязательным
    onClick?: () => void;
}

const NavItem = ({ icon, label, href, onClick }: NavItemProps) => {
    if (href) {
        return (
            <Link
                href={href}
                className="flex items-center py-2 px-4 text-left text-white hover:bg-gray-700 rounded-md transition-colors"
            >
                <span className="mr-2">{icon}</span>
                {label}
            </Link>
        );
    }

    return (
        <button
            onClick={onClick}
            className="flex items-center py-2 px-4 text-left text-white hover:bg-gray-700 rounded-md transition-colors"
        >
            <span className="mr-2">{icon}</span>
            {label}
        </button>
    );
};

export default NavItem;