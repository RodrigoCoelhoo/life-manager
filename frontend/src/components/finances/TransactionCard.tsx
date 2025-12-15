import { LuWallet } from "react-icons/lu";
import { CategoryColors, type TransactionResponseDTO } from "../../services/finances/transaction/transaction.dto";
import { formatBalance } from "../../services/finances/currencies.type";

interface TransactionCardProps {
	transaction: TransactionResponseDTO;
}

export default function TransactionCard({ transaction }: TransactionCardProps) {
	const categoryColor = CategoryColors[transaction.category as keyof typeof CategoryColors] || "#cccccc";

	function hexToRgba(hex: string, alpha: number) {
		const r = parseInt(hex.slice(1, 3), 16);
		const g = parseInt(hex.slice(3, 5), 16);
		const b = parseInt(hex.slice(5, 7), 16);
		return `rgba(${r}, ${g}, ${b}, ${alpha})`;
	}

	return (
		<div className="flex justify-between items-center h-13 bg-background/80 p-4 rounded-lg shadow-lg">
			{/* Icon + Amount */}
			<div className="flex items-center gap-5 flex-1 min-w-0">
				<LuWallet
					size={36}
					className={`p-2 rounded-lg text-textcolor`}
					style={{ backgroundColor: hexToRgba(categoryColor, 0.6) }}
				/>
				<div className="flex flex-col flex-1 min-w-0">
					<span className="text-lg truncate">{transaction.category.replace(/_/g, " ")}</span>
					<span className="text-xs text-gray-400 truncate">
						{new Date(transaction.date).toLocaleDateString()}
					</span>
				</div>
			</div>


			<span
				className={`${transaction.type === "EXPENSE" ? "text-[#F87171]" : "text-[#34D399]"} text-base md:text-sm xl:text-base`}
			>
				{transaction.type === "EXPENSE" ? "- " : "+ "}
				{formatBalance(transaction.amount)}
			</span>
		</div>
	);
}