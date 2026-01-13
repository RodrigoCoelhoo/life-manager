import { useRef, useState } from "react";
import { DateTimeField } from "../common/DateTimeField";
import { ButtonField } from "../common/ButtonField";
import type { WalletResponseDTO } from "../../services/finances/wallet/wallet.dto";
import { Modal } from "../common/Modal";
import { walletService } from "../../services/finances/wallet/wallet.service";
import { SearchList } from "../common/SearchList";
import { MagnifyingGlassIcon } from "@heroicons/react/16/solid";
import CategorySelect from "./CategorySelect";
import type { TransactionFilters } from "../../pages/finances/Transactions";
import { MdClear } from "react-icons/md";

interface TransactionFilterProps {
	filters: TransactionFilters;
  	onApplyFilters: (newFilters: TransactionFilters) => void;
	onClose: () => void;
}

export default function TransactionFilter({ filters, onApplyFilters, onClose }: TransactionFilterProps) {
	const [submitting, setSubmitting] = useState<boolean>(false);
	const [openWalletSearch, setOpenWalletSearch] = useState<boolean>(false);

	const [tempFilters, setTempFilters] = useState<TransactionFilters>({ ...filters });

	const startDateRef = useRef<any>(null);
	const endDateRef = useRef<any>(null);

	const updateTempFilter = <K extends keyof TransactionFilters>(
		key: K,
		value: TransactionFilters[K]
	) => {
		setTempFilters(prev => ({
			...prev,
			[key]: value || undefined,
		}));
	};

	const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
		e.preventDefault();

		const isStartDateValid = startDateRef.current?.validate();
		const isEndDateValid = endDateRef.current?.validate();

		if (!isStartDateValid || !isEndDateValid) {
			return;
		}

		setSubmitting(true);
		onApplyFilters(tempFilters);

		setSubmitting(false);
		onClose();
	};

	return (
		<>
			<div className="
			bg-foreground 
			rounded-xl 
			shadow-lg 
			p-12
			flex flex-col
			gap-4
			text-textcolor
			"
			>
				<button
					onClick={onClose}
					className="absolute top-2 right-4 text-white text-xl hover:text-gray-200 p-2 cursor-pointer"
				>
					✕
				</button>

				<form className="flex flex-col space-y-4 w-90 sm:w-110" onSubmit={handleSubmit}>

					<div className="flex flex-row w-full gap-4">
						<div className="w-1/2">
							<label htmlFor="startDate" className="text-sm mb-1">
								Start Date
							</label>
							<DateTimeField
								ref={startDateRef}
								type="date"
								value={tempFilters.startDate || ""}
								onChange={(value) => updateTempFilter("startDate", value)}
								rules={[
									(val) => {
										if (val && tempFilters.endDate) {
											if (val > tempFilters.endDate) return "Start date cannot be after end date";
										}
										return true;
									},
								]}
							/>
						</div>

						<div className="w-1/2">
							<label htmlFor="endDate" className="text-sm mb-1">
								End Date
							</label>
							<DateTimeField
								ref={endDateRef}
								type="date"
								value={tempFilters.endDate || ""}
								onChange={(value) => updateTempFilter("endDate", value)}
								rules={[
									(val) => {
										if (val && tempFilters.startDate) {
											if (val < tempFilters.startDate) return "End date cannot be before start date";
										}
										return true;
									},
								]}
							/>
						</div>
					</div>

					<div>
						<label htmlFor="date" className="text-sm mb-1">
							Wallet
						</label>
						<div className="flex flex-row gap-2">
							<ButtonField
								value={tempFilters.wallet !== undefined ? tempFilters.wallet.name : ""}
								onClick={() => setOpenWalletSearch(true)}
								icon={<MagnifyingGlassIcon width={16} height={16} />}
								placeholder="Select a wallet"
							/>

							{tempFilters.wallet !== undefined &&
								<div className="w-1/10 text-xl flex items-center justify-center hover:bg-background cursor-pointer rounded-xl">
									<MdClear
										onClick={() => updateTempFilter("wallet", undefined)}
									/>
								</div>}
						</div>
					</div>


					<div>
						<label htmlFor="date" className="text-sm mb-1">
							Category
						</label>
						<CategorySelect
							value={tempFilters.category}
							onChange={(value) => updateTempFilter("category", value)}
							allOption={true}
						/>
					</div>

					<button
						type="submit"
						className="form-submit"
						disabled={submitting}
					>
						Filter
					</button>
				</form>


			</div>

			<Modal isOpen={openWalletSearch} onClose={() => setOpenWalletSearch(false)}>
				<SearchList<WalletResponseDTO>
					fetchItems={walletService.getAllWallets}
					onSelect={(wallet: WalletResponseDTO) => {
						updateTempFilter("wallet", wallet);
						setOpenWalletSearch(false);
					}}
					onClose={() => setOpenWalletSearch(false)}
				/>
			</Modal>
		</>
	);
}