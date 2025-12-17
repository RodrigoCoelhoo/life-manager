import { CategoryColors, CategoryIconComponents } from "../../services/finances/transaction/transaction.dto";
import { formatBalance } from "../../services/finances/currencies.type";
import type { AutomaticTransactionSimple } from "../../services/finances/automatic-transactions/automatic-transactions.dto";

interface BillCardProps {
	bill: AutomaticTransactionSimple;
}

export default function BillDashboardCard({ bill }: BillCardProps) {
	const categoryColor = CategoryColors[bill.category as keyof typeof CategoryColors] || "#cccccc";

	function hexToRgba(hex: string, alpha: number) {
		const r = parseInt(hex.slice(1, 3), 16);
		const g = parseInt(hex.slice(3, 5), 16);
		const b = parseInt(hex.slice(5, 7), 16);
		return `rgba(${r}, ${g}, ${b}, ${alpha})`;
	}

	const IconComponent = CategoryIconComponents[bill.category];

	return (
		<div className="flex justify-between items-center h-13 bg-background/80 p-4 rounded-lg shadow-lg">
			<div className="flex items-center gap-5 flex-1 min-w-0">
				<div
					className={`p-2 rounded-lg flex items-center justify-center`}
					style={{ backgroundColor: hexToRgba(categoryColor, 0.6) }}
				>
					<IconComponent
						size={16}
						className="text-textcolor"
					/>
				</div>
				<div className="flex flex-col flex-1 min-w-0">
					<span className="text-lg truncate">{bill.name}</span>
					<span className="text-xs text-gray-400 truncate">
						{new Date(bill.nextTransactionDate).toLocaleDateString()}
					</span>
				</div>
			</div>

			<span
				className={`${bill.type === "EXPENSE" ? "text-[#F87171]" : "text-[#34D399]"} text-base md:text-sm xl:text-base`}
			>
				{bill.type === "EXPENSE" ? "- " : "+ "}
				{formatBalance(bill.amount)}
			</span>
		</div>
	);
}