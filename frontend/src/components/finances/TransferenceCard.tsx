import { formatBalance } from "../../services/finances/currencies.type";
import type { TransferenceResponseDTO } from "../../services/finances/transference/transference.dto";

interface TransferenceCardProps {
	transference: TransferenceResponseDTO;
}

export default function TransferenceCard({ transference }: TransferenceCardProps) {

	return (
		<div className="flex flex-row justify-between items-center h-13 bg-background/80 p-4 rounded-lg shadow-lg">
			<div className="flex flex-col w-4/10">
				<span className="text-base truncate">{transference.fromWallet.name}</span>
				<span className="text-sm text-[#F87171]">- {formatBalance(transference.fromAmount)}</span>
			</div>
			<div className="w-2/10 flex justify-center items-center">
				<div className="
					h-0
					border-t-[5px] border-t-transparent 
					border-b-[5px] border-b-transparent 
					border-l-10 border-l-textcolor"
				/>
			</div>
			<div className="flex flex-col w-4/10 justify-center items-end">
				<span className="text-lg">{transference.toWallet.name}</span>
				<span className="text-sm text-[#34D399]">+ {formatBalance(transference.toAmount)}</span>
			</div>
		</div>
	);
}