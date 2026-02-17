interface SidebarHeaderProps {
    title: string;
    subtitle: string;
}

const SidebarHeader = ({ title, subtitle }: SidebarHeaderProps) => {
    return (
        <div className="p-4">
            <h2 className="text-lg font-bold text-gray-400">
                {title}
                <br />
                {subtitle}
            </h2>
        </div>
    );
};

export default SidebarHeader;